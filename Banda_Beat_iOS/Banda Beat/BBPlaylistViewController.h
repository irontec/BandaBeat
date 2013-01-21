//
//  BBPlaylistViewController.h
//  Banda Beat
//
//  Created by iker on 27/11/12.
//  Copyright (c) 2012 Irontec S.L. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GAITrackedViewController.h"

@interface BBPlaylistViewController : GAITrackedViewController
@property(weak, nonatomic) IBOutlet UITableView *tableView;
@property(nonatomic) NSMutableArray *playlistArray;
@end
