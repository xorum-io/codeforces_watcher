import UIKit

class ProblemsFiltersViewController: ClosableViewController {
    
    private let tableView = UITableView()
    private let tableAdapter = FiltersTableViewAdapter()
    
    private let tags: [String]
    
    init(_ tags: [String]) {
        self.tags = tags
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setupView()
    }
    
    private func setupView() {
        view.backgroundColor = Palette.white
        title = "filters".localized
        
        buildViewTree()
        setConstraints()
        setupTableView()
    }
    
    private func buildViewTree() {
        view.addSubview(tableView)
    }
    
    private func setConstraints() {
        tableView.edgesToSuperview()
    }
    
    private func setupTableView() {
        tableView.run {
            $0.delegate = tableAdapter
            $0.dataSource = tableAdapter
            $0.separatorStyle = .none
            $0.rowHeight = 58
        }

        tableView.registerForReuse(cellType: FilterTableViewCell.self)
        
        tableAdapter.filterItems = tags.map {
            FilterItem(title: $0, platform: nil, isOn: false, onSwitchTap: {_ in })
        }
    }
}
